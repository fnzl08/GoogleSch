package com.example.googlesch;

import lombok.Data;

@Data
public class SearchDto {
    private String title;
    private String authors;
    private int publicationYear;
    private String publisher;
    private String link;
}
