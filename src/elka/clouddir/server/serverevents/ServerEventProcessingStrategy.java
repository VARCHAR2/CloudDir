package elka.clouddir.server.serverevents;

/**
 * Strategia przetwarzania zdarzenia serwera
 */
public interface ServerEventProcessingStrategy {
    public abstract void process(final ServerEvent event) throws Exception;
}
