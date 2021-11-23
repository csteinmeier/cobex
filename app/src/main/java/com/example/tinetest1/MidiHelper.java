package com.example.tinetest1;

import android.util.Log;

import org.billthefarmer.mididriver.MidiDriver;
import org.jetbrains.annotations.NotNull;

public class MidiHelper implements MidiDriver.OnMidiStartListener {

    public MidiDriver midiDriver;

    public MidiHelper()
    {
        // Instantiate the driver.
        midiDriver = new MidiDriver();
        // Set the listener.
        midiDriver.setOnMidiStartListener(this);
    }

    @Override
    public void onMidiStart() {
        Log.d(this.getClass().getName(), "onMidiStart()");
    }

    public void write(@NotNull byte[] event) {
        midiDriver.write(event);
    }

    public void selectInstrument(int instrument) {
        // Construct a program change to select the instrument on channel 1:
        byte[] event = new byte[2];
        event[0] = (byte)(0xC0 | 0x00); // 0xC0 = program change, 0x00 = channel 1
        event[1] = (byte)instrument;
        // Send the MIDI event to the synthesizer.
        midiDriver.write(event);
    }

    @NotNull
    public int[] config() {
        return midiDriver.config();
    }

    public void start() {
        midiDriver.start();
    }

    public void stop() {
        midiDriver.stop();
    }
}
