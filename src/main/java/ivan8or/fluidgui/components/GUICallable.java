package ivan8or.fluidgui.components;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

public abstract class GUICallable<T> implements Callable<T> {

    protected UUID player_uuid;
    protected Map<String, Object> context;
    protected boolean synchronous;

    public GUICallable(UUID uuid, Map<String,Object> context, boolean synchronous) {
        this.player_uuid = uuid;
        this.context = context;
        this.synchronous = synchronous;
    }

    @Override
    public abstract T call() throws Exception;

    public boolean synchronous() {
        return synchronous;
    }
}
