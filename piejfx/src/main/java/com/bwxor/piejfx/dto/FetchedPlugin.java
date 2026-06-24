package com.bwxor.piejfx.dto;

public record FetchedPlugin(String name, String description, String author, String url) {
    @Override
    public String toString() {
        return name;
    }
}
