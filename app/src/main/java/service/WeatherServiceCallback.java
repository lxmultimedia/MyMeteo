package service;

import data.Channel;

/**
 * Created by dev_z on 09.04.2017.
 */

public interface WeatherServiceCallback {
    void serviceSuccess (Channel channel);
    void serviceFailure (Exception exception);
}
