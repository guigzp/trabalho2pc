import java.util.ArrayList;

public class Elemento {
    private ArrayList<Integer> coordenadas = new ArrayList<>();
    private int indiceCentroide;

    public int getIndiceCentroide() {
        return indiceCentroide;
    }

    public void setIndiceCentroide(int indiceCentroide) {
        this.indiceCentroide = indiceCentroide;
    }

    public Elemento(ArrayList<Integer> coordenadas) {
        this.coordenadas = (ArrayList) coordenadas.clone();
    }

    public ArrayList<Integer> getCoordenadas() {
        return this.coordenadas;
    }
}
