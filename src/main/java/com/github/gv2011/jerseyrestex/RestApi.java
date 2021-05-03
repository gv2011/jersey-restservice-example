package com.github.gv2011.jerseyrestex;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.github.gv2011.jerseyrestex.model.Beanface;
import com.github.gv2011.jerseyrestex.model.Child;
import com.github.gv2011.jerseyrestex.model.Colour;
import com.github.gv2011.jerseyrestex.model.Elementary;

@Path("/rest")
@Produces(MediaType.APPLICATION_JSON)
public class RestApi {

	
	@GET
	@Path("/beanface")
	public Beanface getBeanface() {
		return getExample();
	}
	

	static Beanface getExample() {
        return 
    		new Beanface() {
				@Override
				public String someString() {return "A String";}
				@Override
				public Optional<Beanface> optional() {
					return Optional.empty();
				}
				@Override
				public LocalDateTime time() {
					return LocalDateTime.now();
				}
				@Override
				public Elementary elementary() {
					return new Elementary() {
						@Override
						public Object value() {
							return true;
						}
					};
				}
				@Override
				public Elementary anotherElementary() {
					return new Elementary() {
						@Override
						public Object value() {
							return "lalala";
						}
					};
				}
				@Override
				public Child child() {
					return new Child() {
						@Override
						public String name() {
							return "Fritz";
						}

						@Override
						public long size() {
							return 1672;
						}
					};
				}
				@Override
				public Colour typedString() {
					return new Colour() {
						@Override
						public String toString() {
							return "red";
						}
					};
				}
			}
        ;
    }
	
}
