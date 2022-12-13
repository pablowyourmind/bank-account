package com.github.pablowyourmind.model.datamodel;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.pl.PESEL;

import java.util.ArrayList;
import java.util.List;

@Entity
public class MainAccount {

    @Id
    @PESEL(message = "Nieprawidłowy PESEL")
    private String pesel;

    @Size(min = 1, message = "Imię powinno składać się co najmniej z jednej litery")
    @Size(max = 100, message = "Zbyt długie imię")
    private String firstName;

    @Size(min = 1, message = "Nazwisko powinno składać się co najmniej z jednej litery")
    @Size(max = 100, message = "Zbyt długie nazwisko")
    private String lastName;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "pesel")
    private List<Subaccount> subaccounts = new ArrayList<>();

    public MainAccount() {}

    public MainAccount(String pesel, String firstName, String lastName, List<Subaccount> subaccounts) {
        this.pesel = pesel;
        this.firstName = firstName;
        this.lastName = lastName;
        this.subaccounts = subaccounts;
    }

    public MainAccount(String pesel, String firstName, String lastName) {
        this.pesel = pesel;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getPesel() {
        return pesel;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public List<Subaccount> getSubaccounts() {
        return subaccounts;
    }
}
