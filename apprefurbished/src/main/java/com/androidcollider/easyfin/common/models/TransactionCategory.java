package com.androidcollider.easyfin.common.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ihor Bilous
 */

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(exclude = {"name", "visibility"})
public class TransactionCategory {

    private int id;
    private String name;
    private int visibility;

    public TransactionCategory(int id, String name) {
        this(id, name, 1);
    }
}