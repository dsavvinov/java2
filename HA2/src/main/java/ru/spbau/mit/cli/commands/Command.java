package ru.spbau.mit.cli.commands;


import java.io.IOException;

public interface Command {
    void run() throws IOException;
}
