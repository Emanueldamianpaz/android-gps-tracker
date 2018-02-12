package com.example.ismael.trackgpstokml.util;

/**
 * Escribe el KML mediante XMLSerializer
 * Created by Ismael on 12/02/18.
 */
public class KmlWriter {

    private void crearXML() {

        ArrayList<Punto> puntos = new ArrayList<>();
        puntos.add(new Punto("Casa", "37.3386132,-5.84541779999995"));
        puntos.add(new Punto("Monroy", "37.3483109,-5.84360079999999"));

        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "kml");
            serializer.attribute("", "xmlns", "http://www.opengis.net/kml/2.2");
            for (Punto puntoActual: puntos) {

                serializer.startTag("", "Placemark");

                serializer.startTag("", "name");
                serializer.text(puntoActual.getNombre());
                serializer.endTag("", "name");

                serializer.startTag("", "Point");
                serializer.startTag("", "coordinates");
                serializer.text(puntoActual.getCoordenadas());
                serializer.endTag("", "coordinates");
                serializer.endTag("", "Point");

                serializer.endTag("", "Placemark");

            }
            serializer.endTag("", "kml");
            serializer.endDocument();
            String resultado = writer.toString();
            escribirEnArchivo(this, "puntos.xml", resultado);
            botonLeer.setEnabled(true);
            botonOrdenar.setEnabled(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void cerrarArchivo(Context context, String nombreArchivo, String texto) {
        try {
            OutputStreamWriter filtroSalida = new OutputStreamWriter(context.openFileOutput(nombreArchivo, Context.MODE_PRIVATE));
            filtroSalida.write(texto);
            filtroSalida.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
