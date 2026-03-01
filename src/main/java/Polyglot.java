import org.graalvm.polyglot.*;

//clasa principala - aplicatie JAVA
class Polyglot {
    //metoda privata pentru conversie low-case -> up-case folosind functia toupper() din R
    private static String PythonToUpper(String token) {
        // Construim un context care permite accesul la Python
        try (Context polyglot = Context.newBuilder().allowAllAccess(true).build()) {
            // În Python, metoda .upper() transformă textul în majuscule
            Value result = polyglot.eval("python", "'" + token + "'.upper()");
            return result.asString();
        }
    }

    //metoda privata pentru evaluarea unei sume de control simple a literelor unui text ASCII, folosind PYTHON
    private static int SumCRC(String token){
        //construim un context care ne permite sa folosim elemente din PYTHON
        Context polyglot = Context.newBuilder().allowAllAccess(true).build();
        //folosim o variabila generica care va captura rezultatul excutiei functiei PYTHON, sum()
        //avem voie sa inlocuim anumite elemente din scriptul pe care il construim spre evaluare, aici token provine din JAVA, dar va fi interpretat de PYTHON
        token=token.substring(1,token.length()-1);
        System.out.println("noul subsir din token:"+token);
        String pythonScript="""
        def polinom(text):
            rez=sum(2*ord(ch)**3 -5*ord(ch)**2+3*ord(ch)+10 for ch in text)%100 #2*ch^3+..
            return rez
        polinom('""" +token+ """
        ')
        """;

        Value result = polyglot.eval("python", pythonScript);

        //utilizam metoda asInt() din variabila incarcata cu output-ul executiei, pentru a mapa valoarea generica la un Int
        int resultInt = result.asInt();
        // inchidem contextul Polyglot
        polyglot.close();

        return resultInt;
    }

    //functia MAIN
    public static void main(String[] args) {
        System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");
        //construim un context pentru evaluare elemente JS
        Context polyglot = Context.create();
        //construim un array de string-uri, folosind cuvinte din pagina web:  https://chrisseaton.com/truffleruby/tenthings/
        Value array = polyglot.eval("js", "[\"If\",\"we\",\"run\",\"the\",\"java\"];");
        //pentru fiecare cuvant, convertim la upcase folosind R si calculam suma de control folosind PYTHON
        for (int i = 0; i < array.getArraySize();i++){
            String element = array.getArrayElement(i).asString();
            String upper = PythonToUpper(element);
            int crc = SumCRC(upper);


            System.out.println(upper + " -> " + crc);
        }
        // inchidem contextul Polyglot
        polyglot.close();
    }
}
