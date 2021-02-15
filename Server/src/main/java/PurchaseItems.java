import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.v3.oas.annotations.media.Schema;
/**
 * PurchaseItems
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-02-06T10:56:49.513Z[GMT]")
public class PurchaseItems {
    @SerializedName("ItemID")
    private String itemID = null;

    @SerializedName("numberOfItems:")
    private Integer numberOfItems = null;

    public PurchaseItems itemID(String itemID) {
        this.itemID = itemID;
        return this;
    }

    /**
     * Get itemID
     * @return itemID
     **/
    @Schema(description = "")
    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public PurchaseItems numberOfItems(Integer numberOfItems) {
        this.numberOfItems = numberOfItems;
        return this;
    }

    /**
     * Get numberOfItems
     * @return numberOfItems
     **/
    @Schema(description = "")
    public Integer getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(Integer numberOfItems) {
        this.numberOfItems = numberOfItems;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PurchaseItems purchaseItems = (PurchaseItems) o;
        return Objects.equals(this.itemID, purchaseItems.itemID) &&
                Objects.equals(this.numberOfItems, purchaseItems.numberOfItems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemID, numberOfItems);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class PurchaseItems {\n");

        sb.append("    itemID: ").append(toIndentedString(itemID)).append("\n");
        sb.append("    numberOfItems: ").append(toIndentedString(numberOfItems)).append("\n");
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