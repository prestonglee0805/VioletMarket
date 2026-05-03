package violetmarket.interfaces;

import violetmarket.enums.ItemCategory;
import violetmarket.enums.ItemCondition;
import violetmarket.model.Listing;

import java.util.List;

public interface Searchable {
    List<Listing> search(String keyword);
    List<Listing> filterByCategory(ItemCategory category);
    List<Listing> filterByCondition(ItemCondition condition);
    List<Listing> sortByPrice(boolean ascending);
}
