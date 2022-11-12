/*
 *     Kingdoms Plugin
 *     Copyright (C) 2022  Fisher2911
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.fisher2911.fisherlib.economy;

import io.github.fisher2911.fisherlib.message.Message;

public enum TransactionResultType {

    WITHDRAW_SUCCESS(true, Message.BANK_WITHDRAW_SUCCESS),
    DEPOSIT_SUCCESS(true, Message.BANK_DEPOSIT_SUCCESS),
    NOT_ALLOWED(false, Message.BANK_NO_PERMISSION),
    NOT_ENOUGH_FUNDS(false, Message.BANK_NOT_ENOUGH_FUNDS),
    BANK_NOT_LARGE_ENOUGH(false, Message.BANK_NOT_LARGE_ENOUGH);

    private final boolean isSuccessful;
    private final Message message;

    TransactionResultType(boolean isSuccessful, Message message) {
        this.isSuccessful = isSuccessful;
        this.message = message;
    }

    public TransactionResult of(double balance) {
        return new TransactionResult(this, balance);
    }

    public Message getMessage() {
        return message;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }
}
