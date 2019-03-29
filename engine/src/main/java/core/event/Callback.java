package core.event;

public interface Callback {

    public abstract void invoke(Event e) throws CallbackStackInterruption;

    public abstract int priority();

}