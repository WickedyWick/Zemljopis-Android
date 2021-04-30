# Android klijent za zemljopis

Android mobilna verzija za zemljopis / nacionalnu geografiju

# SERVER

* [Server](https://github.com/WickedyWick/zemljopis) - Server , glavni projekat

# DOZVOLE 
Zabranjeno kopiranje i korišćenje koda osim za lične potrebe (testiranje i lokalno igranje za maksimalno 2 igrača).

## PLANOVI
- Trenutni planovi i ciljevi je da android klijent ima sve funkcionalnosti kao i desktop verzija

## STATE
    In early development

## Potrebne alatke, tehnologije, frameworkovi

* [Android Studio](https://developer.android.com/studio) - android studio
* [Java](https://www.java.com/download) - java

## Instalacija i konfiguracija

- Nakon instalacije android studija i jave , otvoriti projekat , promeniti parametre URI konstruktora u adresu servera (MainActivity.java line 32 i GameActivity.java line 193)
- Što se tiče adrese servera nisam testirao kako localhost funkcioniše sa povezanim telefonom, pa sam samo portforwardovao projekat i koristio javnuadresu:3000 kao adresu servera
- Za instalaciju aplikacije potrebno je povezati telefon sa računarom , omogućiti ADB USB Debugging i kompajlovati aplikaciju (preporučeno u debug modu)
![primer](https://github.com/WickedyWick/Zemljopis-Android/blob/main/demo/compileSetup.png)


### POZNATI BUGOVI
- Potrebno restartovati aplikaciju u koliko zelite da promenite sobu
- Neki bugovi prilikom igranja igre
- UI izgleda drugačije na različitim rezolucijama ili veličinama ekrana tako da je moguće da se neće videti na nekim telefonima
- Trenutno samo testirano na Lenovo K6 telefonu (**https://www.mobilnisvet.com/mobilni/5655/Lenovo/K6**)

### TODO 
- Da se upgraduje do desktop verzije
- Offline mode

### SCREENSHOTOVI
Landing

<img src="https://github.com/WickedyWick/Zemljopis-Android/blob/main/demo/home.jpg" alt="Start Round" width="360" height="720">

Round Start

<img src="https://github.com/WickedyWick/Zemljopis-Android/blob/main/demo/pre.jpg" alt="Start Round" width="360" height="720">

Round End i evaluacija

<img src="https://github.com/WickedyWick/Zemljopis-Android/blob/main/demo/after.jpg" alt="Start Round" width="360" height="720">

### NOTE
- Neće biti dodate nove funkcionalnosti za TTM takmičenje osim možda bug fixova , sto čini ovu verziju finalnu za takmičenje.
- Baza podataka je još jako mala
- Glavni cilj mog projekta je zabava, ali to nije jedina stvar što doprinosi. Iz mog ličnog iskustva i okruženja primetio sam da tokom godina počnu da se zaboravljaju stvari naučene iz škole ako se ne upotrebljavaju , moj projekat omogućava da se upotrebljava znanje iz oblasti geografije. Odlučio sam da napravim (portujem , kako god) online verziju igre poznatu u narodu kao nacionalna geografija ili zemljopis. Sa sve većom upotrebnom računara, mobilnih telefona , tableta ... potrebno je imati mogućnost igrati igru i na tim uređajima , pogotovo u trenutnoj pandemiji. Ovaj projekat ne pomaže direktno studentima ali je tu da pruži zabavu sa drugarima i u isto vreme proširi ili održava znanje o geografiji. Trenutno stanje mobilne aplikacije je loše ali pokazuje da je rešenje moguće.
