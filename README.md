#Islands

Questa cartella contiene:

* Un progetto zippato di IntelliJ-IDEA suddiviso in 7 sottomoduli:
  * `actodes`: i sorgenti di ActoDeS versione 1.0.2 su cui ActoDatA è basato. È nel progetto semplicemente per avere facilemente una documentazione di supporto disponibile nel workspace per lo sviluppo di ActoDatA, ma è anche una dependency degli altri moduli;
  * `javautils`: modulo con varie utility generiche in Java, quasi tutti gli altri moduli dipendono da questo; concetti come `Promise` sono definiti qui.
  * `actodata-core`: ActoDatA, con le parti più generiche della libreria; concetti come `Controller`, `Engine`, `Acquirer`, etc... sono definiti qui (dipende da actodes e javautils);
  * `gpj`: libreria scritto da Giuseppe Petrosino per il genetic programming (opzionalmente distribuito), che permette di definire algoritmi evolutivi per evolvere alberi che rappresentano programmi funzionali (dipende solo da javautils);
  * `islands`: libreria scritta da Luca Calderini e Paolo D'Alessandro per la simulazione di algoritmi evolutivi basati su isole. (dipende dalle librerie sopra citate)
  * `average`: esempio di implementazione della libreria `islands` basato sull'approssimazione della media.
  * `pattern`: esempio di implementazione della libreria `islands` basato sul pattern guessing.
 
 Notare che i progetti sono basati su Java 16.