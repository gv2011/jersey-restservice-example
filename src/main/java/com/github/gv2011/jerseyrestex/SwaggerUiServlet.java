package com.github.gv2011.jerseyrestex;

import static com.google.common.base.Verify.verify;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.OptionalInt;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.google.common.io.ByteStreams;

public class SwaggerUiServlet extends HttpServlet {

	private static final String INDEX_HTML = "index.html";
	
	public static final String REQUEST_PREFIX = "requestPrefix";
	public static final String SWAGGER_UI_VERSION = "swaggerUiVersion";
	public static final String API_URL = "apiUrl";
	public static final String DISABLE_CACHE = "disableCache";

	private static final long serialVersionUID = 4297232020959550025L;

	private static final Logger LOG = getLogger(SwaggerUiServlet.class);
    
    private static final Duration CACHE_EXPIRY_TIME = Duration.ofDays(1);
    

    private boolean disableCache = false;
    
    private Optional<String> requestPrefix = Optional.empty();
    private Optional<String> version = Optional.empty();
    private Optional<String> resourcePrefix = Optional.empty();
    private Optional<String> indexHtml = Optional.empty();

    @Override
    public void init(final ServletConfig config) throws ServletException {
    	super.init(config);
        disableCache = ofNullable(config.getInitParameter(DISABLE_CACHE)).map(Boolean::parseBoolean).orElse(false);
        version = Optional.of(config.getInitParameter(SWAGGER_UI_VERSION));
        requestPrefix = Optional.of(
    		ofNullable(config.getInitParameter(REQUEST_PREFIX))
    		.orElse("/webjars/swagger-ui/"+version.get()+"/")
		);
        verify(requestPrefix.get().endsWith("/"));
		resourcePrefix = Optional.of("/META-INF/resources/webjars/swagger-ui/"+version.get()+"/");
		final String apiUrl = requireNonNull(config.getInitParameter(API_URL));
		try(final InputStream in = requireNonNull(getClass().getResourceAsStream(resourcePrefix.get()+ INDEX_HTML))){
			indexHtml = Optional.of(
				new String(ByteStreams.toByteArray(in), UTF_8)
				.replace("url: \"https://petstore.swagger.io/v2/swagger.json\"", "url: \"" + apiUrl + "\"")
			);
		} catch (IOException e) {
			throw new ServletException(e);
		}
    }

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String requestPath = request.getRequestURI();
        final OptionalInt error;
		final Optional<String> rp = getResourcePath(requestPath);
        final OutputStream out = response.getOutputStream();
		if(rp.isEmpty()) {        	
        	error = OptionalInt.of(HttpServletResponse.SC_NOT_FOUND);
        }
        else {
    		final String resourcePath = rp.get();
    		final String eTagName = getETagName(resourcePath);
        	if (!disableCache && (checkETagMatch(request, eTagName) || checkLastModify(request))) {
        		error = OptionalInt.empty();
        		response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            }
        	else {
        		if(isIndexFile(resourcePath)) {
        			out.write(indexHtml.get().getBytes(UTF_8));
        			error = OptionalInt.empty();
        		}
        		else {
	        		final InputStream inputStream = getClass().getResourceAsStream(resourcePath);
	        		if (inputStream != null) {
	        			try {
	        				if (!disableCache) prepareCacheHeaders(response, eTagName);
	        				final String filename = getFileName(resourcePath);
	        				final String mimeType = getServletContext().getMimeType(filename);
	        				response.setContentType(mimeType != null ? mimeType : "application/octet-stream");
	        				ByteStreams.copy(inputStream, out);
	        			} finally {
	        				inputStream.close();
	        			}
	        			error = OptionalInt.empty();
	        		} else {
	        			error = OptionalInt.of(HttpServletResponse.SC_NOT_FOUND);
	        			response.sendError(HttpServletResponse.SC_NOT_FOUND);
	        		}
        		}
        	}
    	}
        LOG.info("request path: {}, resource path: {}", requestPath, rp);
        if(error.isPresent()) response.sendError(error.getAsInt());
        else out.flush();
    }

	private boolean isIndexFile(String resourcePath) {
		return resourcePath.equals(resourcePrefix.get()+INDEX_HTML);
	}

	private Optional<String> getResourcePath(final String requestPath) {
		Optional<String> resourcePath;
		if(!requestPath.startsWith(requestPrefix.get())) resourcePath = Optional.empty();
		else {
			String suffix = requestPath.substring(requestPrefix.get().length());
			if(suffix.equals("index.html.gz")) resourcePath = Optional.empty();
			else {
				if(suffix.isEmpty()) suffix = INDEX_HTML;
				resourcePath = Optional.of(resourcePrefix.get() + suffix);
			}
		}
		return resourcePath;
	}

    private String getFileName(String resourcePath) {
        String[] tokens = resourcePath.split("/");
        return tokens[tokens.length - 1];
    }

	    
    private String getETagName(String webjarsResourceURI) {
        return getFileName(webjarsResourceURI) + "_" + version;
    }
	    
    private boolean checkETagMatch(HttpServletRequest request, String eTagName) {
    	return 	ofNullable(request.getHeader("If-None-Match")).map(t->t.equals(eTagName)).orElse(false);
    }

    private boolean checkLastModify(HttpServletRequest request) {
       long last = request.getDateHeader("If-Modified-Since");
       return (last == -1L? false : (last - System.currentTimeMillis() > 0L));
    }
	    
    private void prepareCacheHeaders(HttpServletResponse response, String eTag) {	        
        response.setHeader("ETag", eTag);
        final Instant now = Instant.now();
		final Instant expires = now.plus(CACHE_EXPIRY_TIME);
        response.setDateHeader("Expires", expires.toEpochMilli());
        response.addDateHeader("Last-Modified", expires.toEpochMilli()); 
        response.addHeader("Cache-Control", "private, max-age=" + CACHE_EXPIRY_TIME.getSeconds());
    }

}
