package com.trollpixel.signshop.location;

import java.util.function.Function;

public interface LocationParser<T> extends Function<SerializedLocation, T> {

}
