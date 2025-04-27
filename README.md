# Központi Időpontfoglaló Alkalmazás

A projekt célja egy modern, mobilbarát időpontfoglaló rendszer megvalósítása Android platformra.
Responzív felhasználói felületen támogatja a kormányablakok közötti foglalásokat, 
és lehetőséget ad a regisztrált felhasználóknak az időpontjaik kezelésére.

### Tehnológia:
1. Android Studio (Java)
2. Firebase Authentication – regisztráció, bejelentkezés
3. Firestore – adatok tárolása (felhasználók, foglalások)

### Elrendezés (layout)
1. ConstraintLayout
2. LinearLayout
3. GridLayout

### Alkalmazás felépítése (MVC)
1. Modellek: User, Office, Service, Booking
2. View-k: XML layout fájlok, dinamikus komponensek (időponttáblázat)
3. Controller-ek: Login, Register, Profile, Booking, BookingTime, Reservations (Activities), stb.

### Autentikáció
1. Firebase alapú regisztráció és bejelentkezés
2. Validáció e-mail, jelszó, telefonszám és felhasználónév alapján

### Firebase - Firestore
1. CRUD műveletek:
    * Új tétel: felhasználó, időpontfoglalás
    * Lekérdezés: időpontfoglalás dátum, felhasználó szerint
    * Módosítás: Felhasználói adatok
    * Törlés: időpontfoglalás
3. Foglalások szűrése adott nap, hivatal és szolgáltatás alapján, idő szerinti rendezéssel
2. Felhasználó saját foglalásai, dátum, idő csökkenő, hivatal, szolgáltatás növekvő sorrendben
3. Limitált lekérdezések

### Android rendszererőforrások
1. Telefonhívás indítása a kiválasztott hivatal számán
2. Google Maps megnyitása hivatal címére pozicionálva
3. Engedélykérések CALL_PHONE, POST_NOTIFICATIONS

### Háttérszolgáltatások
1. Értesítés (Notification) foglalás mentésekor
2. Emlékeztető (AlarmManager) foglalás előtti napra beállítva

### Felhasználói élmény
1. Responzív megjelenés
2. Dinamikus, strukturált időponttáblázat
3. Egységes animációk (fade, slide, scale)
4. Activity-váltás, visszalépés animációval

