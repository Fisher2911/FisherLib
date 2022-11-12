/*
 *     FisherLib
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

package io.github.fisher2911.fisherlib.message;

public record Message(String path) {

    public static Message path(final String path) {
        return new Message(path);
    }

    public static final Message ADMIN_COMMAND_HELP_HEADER = path("admin-command-help-header");
    public static final Message ADMIN_COMMAND_HELP_FOOTER = path("admin-command-help-footer");
    public static final Message ADMIN_COMMAND_HELP_FORMAT = path("admin-command-help-format");
    public static final Message COMMAND_HELP_HEADER = path("command-help-header");
    public static final Message COMMAND_HELP_FOOTER = path("command-help-footer");
    public static final Message COMMAND_HELP_FORMAT = path("command-help-format");
    public static final Message USER_DATA_LOAD_ERROR = path("user-data-load-error");
    public static final Message INVALID_COMMAND_EXECUTOR = path("invalid-command-executor");
    public static final Message NO_COMMAND_PERMISSION = path("no-command-permission");
    public static final Message BANK_WITHDRAW_SUCCESS = path("bank-withdraw-success");
    public static final Message BANK_DEPOSIT_SUCCESS = path("bank-deposit-success");
    public static final Message BANK_NO_PERMISSION = path("bank-no-permission");
    public static final Message BANK_NOT_ENOUGH_FUNDS = path("bank-not-enough-funds");
    public static final Message BANK_NOT_LARGE_ENOUGH = path("bank-not-large-enough");

    public String getConfigPath() {
        return this.toString().toLowerCase().replace("_", "-");
    }

}
