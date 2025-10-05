package com.starkeys.be.dto;

public record PaperSimple(
        String paperId,
        String title,
        String doi,
        String paperAbstract,
        String[] fields
) {
    public static PaperSimple of(EsPaper esPaper) {
        return new PaperSimple(
                esPaper.getPaperId(),
                esPaper.getTitle(),
                esPaper.getDoi(),
                esPaper.getPaperAbstract(),
                esPaper.getFields()
        );
    }
}
