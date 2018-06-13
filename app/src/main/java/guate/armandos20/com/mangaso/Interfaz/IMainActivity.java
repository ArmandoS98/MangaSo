package guate.armandos20.com.mangaso.Interfaz;


import guate.armandos20.com.mangaso.Entidades.Home;

public interface IMainActivity {

    void createNewNote(String title, String content);

    void onNoteSelected(Home note);

    void updateNote(Home note);

    void deleteNote(Home note);
}
