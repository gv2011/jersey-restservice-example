package com.github.gv2011.jerseyrestex;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.introspect.BasicClassIntrospector;

final class PatchedClassIntrospector extends BasicClassIntrospector {
	
	@Override
    protected boolean _isStdJDKCollection(JavaType type) {
        return super._isStdJDKCollection(type) ? true : PatchedModelResolver.isGuavaCollection(type);
    }

}
