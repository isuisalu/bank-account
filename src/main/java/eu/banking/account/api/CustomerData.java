package eu.banking.account.api;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CustomerData {
    @NotNull
    private String name;
}
