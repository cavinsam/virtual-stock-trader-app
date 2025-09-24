package com.vst.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tutorials")
public class Tutorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob // Large Object, suitable for long text content
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    // --- CONSTRUCTORS ---
    public Tutorial() {}

    public Tutorial(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // --- GETTERS AND SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
