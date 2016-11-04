Nutzung:<br />
Der Kommando-Zeilen-Client sucht zu einem eingegebenen Wort passende Reim-Wörter.

Alleinstellungsmerkmale:<br />
Der Algorithmus nutzt phonetische IPA-Transkriptionen.
Er findet mit einer unscharfen Suche auch unreine Reime und filtert falsche Schreibreime (im Gegensatz zu simplen String-Vergleichen). Der Algorithmus funktioniert mit jeder Sprache und ist sehr flexibel einstellbar.

Funktionsweise:<br />
Er verfügt über ein, aus dem deutschen Wiktionary-Dump geparstes, WORT - IPA-LAUTSCHRIFT Dictionary.
Die einzelnen Lautschrift-Unicodes werden nach phonetischen Gesichtspunkten attributiert. So ist es möglich detaillierter zu vergleichen(z.B. klingt "i" ähnlicher zu "e" als zu "u").
Anhand von Gewichten und ähnlichem werden verschiedene Eigenschaften unserer gesprochenen Sprache nachgebildet. Zum Vergleich wird ein dezimaler "Reim-Faktor" errechnet, nach dem die Ergebnisse sortiert werden.


Beispiel-Suche nach "Handy": <br />
0,994 -           Crescendi    -   kʀɛˈʃɛndi  <br />
0,984 -               Dandy    -   ˈdɛndi  <br />
0,983 -       Modus Vivendi    -   ˈmoːdʊs viˈvɛndi  <br />
0,980 -              trendy    -   ˈtʀɛndi    <br />
0,979 -       Venia Legendi    -   ˈveːni̯a leˈɡɛndi  <br />
0,978 -              Brandy    -   ˈbʀɛndi   <br />
0,977 -              Shanty    -   ˈʃɛnti  <br />
0,974 -              Selfie    -   ˈsɛlfi  <br />
0,973 -               Hansi    -   ˈhanzi  <br />
0,966 -               Zenzi    -   ˈʦɛnʦi  <br />
0,965 -              Caddie    -   ˈkɛdi  <br />
0,963 -         Sprechtempi    -   ˈʃpʀɛçˌtɛmpi  <br />
0,962 -            Festival    -   ˈfɛstivl̩  <br />
0,962 -   Open-Air-Festival    -   ˈoːpn̩ˈʔɛːɐ̯ˌfɛstivl̩  <br />
0,961 -   Modi significandi    -   ˈmɔdi/ˈmoːdi zɪɡnifiˈkandi  <br />
0,961 -  Modus significandi    -   ˈmɔdʊs/ˈmoːdʊs zɪɡnifiˈkandi  <br />
0,961 -               Tempi    -   ˈtɛmpi  <br />
0,961 -           Redetempi    -   ˈʀeːdəˌtɛmpi  <br />
0,961 -          Sugardaddy    -   ˈʃʊɡɐˌdɛdi  <br />
0,961 -               Teddy    -   ˈtɛdi  <br />
