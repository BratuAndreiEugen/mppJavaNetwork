package network.objectprotocol;

import java.time.LocalDate;

public class FilterRequest implements Request{
    private LocalDate date;

    public FilterRequest(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
