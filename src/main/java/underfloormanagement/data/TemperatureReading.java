package underfloormanagement.data;

public class TemperatureReading {
  long aanvoerTemp;
  long retourTemp;
  long kastTemp;

  public TemperatureReading() {
    aanvoerTemp = 0;
    retourTemp = 0;
    kastTemp = 0;
  }

  @Override
  public String toString() {
    return String.format("Temperatuur:\nAanvoer: %d\nRetour: %d\nKast: %d\n", this.aanvoerTemp, this.retourTemp,
        this.kastTemp);
  }

  public long getAanvoerTemp() {
    return aanvoerTemp;
  }

  public long getKastTemp() {
    return kastTemp;
  }

  public long getRetourTemp() {
    return retourTemp;
  }

  public void setAanvoerTemp(long aanvoerTemp) {
    this.aanvoerTemp = aanvoerTemp;
  }

  public void setKastTemp(long kastTemp) {
    this.kastTemp = kastTemp;
  }

  public void setRetourTemp(long retourTemp) {
    this.retourTemp = retourTemp;
  }
}
