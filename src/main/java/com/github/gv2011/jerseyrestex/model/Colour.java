package com.github.gv2011.jerseyrestex.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(example="red")
public interface Colour extends TypedString<Colour>{

	String toString();

}
