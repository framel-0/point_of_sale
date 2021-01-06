package net.sipconsult.pos.ui.products

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import net.sipconsult.pos.R
import net.sipconsult.pos.data.models.ProductItem
import java.util.*

class ProductListAdapter(private val onProductClick: (ProductItem) -> Unit) :
    RecyclerView.Adapter<ProductViewHolder>(), Filterable {

    private var _products: ArrayList<ProductItem> = arrayListOf()
    var productFilter: ArrayList<ProductItem> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
//        val binding =
//            ProductListItemBinding.inflate(layoutInflater)
        val itemView = layoutInflater.inflate(R.layout.list_item_product, parent, false)
        return ProductViewHolder(itemView, onProductClick)
    }

    fun setProducts(products: ArrayList<ProductItem>) {
        _products = products
        productFilter = products
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = productFilter.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productFilter[position]
        holder.bind(product, position)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                productFilter = if (charSearch.isEmpty()) {
                    _products
                } else {
                    val resultList: ArrayList<ProductItem> = arrayListOf()
                    for (product in _products) {

                        val productBarcode = product.barcode ?: ""
                        val productCode = product.code

                        if ((product.description.toLowerCase(Locale.ROOT)
                                .contains(charSearch.toLowerCase(Locale.ROOT)) ||
                                    productBarcode.toLowerCase(Locale.ROOT)
                                        .contains(charSearch.toLowerCase(Locale.ROOT)) ||
                                    productCode.toLowerCase(Locale.ROOT)
                                        .contains(charSearch.toLowerCase(Locale.ROOT)))
                        ) {
                            resultList.add(product)
                        }


                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = productFilter
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                productFilter = results?.values as ArrayList<ProductItem>

                notifyDataSetChanged()
            }

        }
    }
}