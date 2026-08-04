package com.bwxor.piejfx.dto;

public record FetchedPlugin(
        String name,
        String slug,
        String description,
        String author,
        boolean verified,
        String downloadUrl,
        String homepageUrl
) {
    @Override
    public String toString() {
        return name;
    }
}
