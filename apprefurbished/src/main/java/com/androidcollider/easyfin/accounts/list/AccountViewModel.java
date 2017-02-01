package com.androidcollider.easyfin.accounts.list;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Ihor Bilous
 */

@Getter
@AllArgsConstructor
class AccountViewModel {

    private int id;
    private String name;
    private String amount;
    private int type;
}
