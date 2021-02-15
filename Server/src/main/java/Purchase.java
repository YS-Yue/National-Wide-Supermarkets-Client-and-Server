import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
/**
 * Purchase
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-02-06T10:56:49.513Z[GMT]")
public class Purchase {
    @SerializedName("items")
    private List<PurchaseItems> items = null;

    public Purchase items(List<PurchaseItems> items) {
        this.items = items;
        return this;
    }

    public Purchase addItemsItem(PurchaseItems itemsItem) {
        if (this.items == null) {
            this.items = new ArrayList<PurchaseItems>();
        }
        this.items.add(itemsItem);
        return this;
    }

    /**
     * Get items
     * @return items
     **/
    @Schema(description = "")
    public List<PurchaseItems> getItems() {
        return items;
    }

    public void setItems(List<PurchaseItems> items) {
        this.items = items;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Purchase purchase = (Purchase) o;
        return Objects.equals(this.items, purchase.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Purchase {\n");

        sb.append("    items: ").append(toIndentedString(items)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}
