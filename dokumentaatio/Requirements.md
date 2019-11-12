# Vaatimusmäärittely
## Sovelluksen tarkoitus

Sling sovelluksen avulla käyttäjä voi helposti kirjoittaa ohjelman, joka kommunikoi David DBMS-ohjelman kanssa. Crossbow ohjelman avulla hän voi graafisessa ympäristössä selata ja muokata Davidin tietokanta olioita. (Kutsun tietokantaolioiksi sellaisia asioita kuten relaatiomuuttuja (relvar), eheysehto (constraint), näkymä (view) jne.) Crossbow käyttää Slingiä kommunikoidakseen Davidin kanssa.

## Käyttäjät
Käyttöoikeudet määritellään Davidissa. Sling (ja sitä kautta Crossbow) kirjautuvat käyttäjätunnuksella ja salasanalla Davidiin ja saavat sitä kautta tietyt oikeudet David-tietokantaan.

## Käyttöliittymäluonnos
Crossbown ikkuna on jaettu horisontaalisesti kahteen alueeseen. Vasemmalla listataan tietokantaoliot (jotka näkyvät käyttäjälle) puunäkymässä. Tämän kautta käyttäjä voi rajatusti lisätä, poistaa ja muokata tietokantaolioita. Oikealla puolella on tabbed pane, jossa voi olla yksi tai useampi tabi. Yksi tabi jakautuu vertikaalisesti niin että yläosassa näkyy viimeisimmän kyselyn tulos taulukkomuodossa. Taulukon dataa voi editoida, deletoida ja lisätä siihen uusia monikkoja, sillä oletuksella, että kyselyn monikko on deletoitavissa/lisättävissä/muokattavissa kyselyn määrittelemän näkymän . Alaosassa on komentoikkuna, johon voi kirjoittaa lauseita ja lähettää ne Davidille suoritettaviksi. Tuloksena on joko virheilmoitus tai suorittamisen tulokset.
