package alexandre.blended.util;

import java.util.UUID;

public abstract class Utilities {

    public static String uuid(){
        return UUID.randomUUID().toString();
    }
}
