package com.jpromi.darts.backend.models;

import lombok.*;

import java.util.ArrayList;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailObject {
    private ArrayList<String> to;
    private ArrayList<String> cc;
    private ArrayList<String> bcc;
    private String subject;
    private String body;
    private String htmlBody;
    private ArrayList<java.io.File> attachments;
}
