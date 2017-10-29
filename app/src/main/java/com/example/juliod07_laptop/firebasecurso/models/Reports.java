package com.example.juliod07_laptop.firebasecurso.models;

/**
 * Created by JulioD07-LAPTOP on 8/28/2017.
 */

public class Reports {

    private String Titulo;
    private String Descripcion;
    private String image;
    private String username;
    private String userImage;

    public Reports() {

    }

    public Reports(String titulo, String descripcion, String image, String username, String userImage) {
        Titulo = titulo;
        Descripcion = descripcion;
        this.image = image;
        this.username = username;
        this.userImage = userImage;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitulo() {

        return Titulo;
    }

    public void setTitulo(String titulo) {
        Titulo = titulo;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
