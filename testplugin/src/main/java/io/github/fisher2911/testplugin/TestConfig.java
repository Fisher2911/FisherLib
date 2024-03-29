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

package io.github.fisher2911.testplugin;

import io.github.fisher2911.common.item.ItemBuilder;
import io.github.fisher2911.config.annotation.Config;
import io.github.fisher2911.config.annotation.ConfigPath;

import java.util.List;

@Config(filePath = {"test-config.yml"})
public class TestConfig {

    private TestConfig() {}

    @ConfigPath()
    private String first;

    @ConfigPath("name")
    private String name;

    @ConfigPath
    private int number;

    @ConfigPath("list-value")
    private List<Integer> list;

    @ConfigPath("nested.test")
    private String nestedTest;

    @ConfigPath("item")
    private ItemBuilder item;

    @ConfigPath("items")
    private List<ItemBuilder> items;

    public TestConfig(String first) {
        this.first = first;
    }

    public String getFirst() {
        return this.first;
    }

    public String getName() {
        return this.name;
    }

    public int getNumber() {
        return this.number;
    }

    public List<Integer> getList() {
        return this.list;
    }

    public String getNestedTest() {
        return this.nestedTest;
    }

    public ItemBuilder getItem() {
        return item;
    }

}
