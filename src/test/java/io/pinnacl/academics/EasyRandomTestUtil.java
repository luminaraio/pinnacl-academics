package io.pinnacl.core;

import io.pinnacl.RecordFactory;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

public class EasyRandomTestUtil {
    private final static EasyRandomParameters parameters =
            new EasyRandomParameters().objectFactory(new RecordFactory());
    public static EasyRandom easyRandom = new EasyRandom(parameters);
}
