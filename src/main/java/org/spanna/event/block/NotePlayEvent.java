package org.spanna.event.block;

import org.spanna.Instrument;
import org.spanna.Note;
import org.spanna.block.Block;
import org.spanna.event.Cancellable;
import org.spanna.event.HandlerList;

/**
 * Called when a note block is being played through player interaction or a
 * redstone current.
 */
public class NotePlayEvent extends BlockEvent implements Cancellable {

    private static HandlerList handlers = new HandlerList();
    private Instrument instrument;
    private Note note;
    private boolean cancelled = false;

    public NotePlayEvent(Block block, Instrument instrument, Note note) {
        super(block);
        this.instrument = instrument;
        this.note = note;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    /**
     * Gets the {@link Instrument} to be used.
     *
     * @return the Instrument;
     */
    public Instrument getInstrument() {
        return instrument;
    }

    /**
     * Gets the {@link Note} to be played.
     *
     * @return the Note.
     */
    public Note getNote() {
        return note;
    }

    /**
     * Overrides the {@link Instrument} to be used.
     *
     * @param instrument the Instrument. Has no effect if null.
     */
    public void setInstrument(Instrument instrument) {
        if (instrument != null) {
            this.instrument = instrument;
        }

    }

    /**
     * Overrides the {@link Note} to be played.
     *
     * @param note the Note. Has no effect if null.
     */
    public void setNote(Note note) {
        if (note != null) {
            this.note = note;
        }
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
