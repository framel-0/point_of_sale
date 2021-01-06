package net.sipconsult.pos.ui.products

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.android.synthetic.main.products_fragment.*
import kotlinx.coroutines.launch
import net.sipconsult.pos.R
import net.sipconsult.pos.ScanningActivity
import net.sipconsult.pos.data.models.CartItem
import net.sipconsult.pos.data.models.ProductItem
import net.sipconsult.pos.data.repository.shoppingCart.ShoppingCartRepository
import net.sipconsult.pos.ui.base.ScopedFragment
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.util.*


class ProductsFragment : ScopedFragment(), KodeinAware {

    override val kodein by closestKodein()

    private val viewModelFactory: ProductViewModelFactory by instance()

    private lateinit var viewModel: ProductsViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.products_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(ProductsViewModel::class.java)

        bindUI()
    }

    private fun bindUI() = launch {
        val locations = viewModel.locations.await()
        if (view != null) {
            locations.observe(viewLifecycleOwner, Observer { lcs ->
                if (lcs == null) return@Observer
                val size = lcs.size
//            Toast.makeText(context, "Locations size $size",Toast.LENGTH_SHORT).show()

            })
        }
        val products = viewModel.products.await()
        if (view != null) {
            products.observe(viewLifecycleOwner, Observer { pdts ->
                if (pdts == null) return@Observer
                groupLoadingProducts.visibility = View.GONE
                setupProductRecyclerView(pdts as ArrayList<ProductItem>)
                viewModel.productItems = pdts
            })

            buttonScanBarcode.setOnClickListener {
//            Toast.makeText(activity, "Scan", Toast.LENGTH_LONG).show()
//            startInternalScanSunmi()
                startExternalScanSunmi()
            }
        }


    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun Fragment.hideKeyboard() {
        view?.let {
            activity?.hideKeyboard(it)
        }
    }

    private fun setupSearchView(productListAdapter: ProductListAdapter) {
        searchProducts.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                hideKeyboard()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                productListAdapter.filter.filter(newText)
                return false
            }

        })
    }

    private fun setupProductRecyclerView(products: ArrayList<ProductItem>) {
        val productRecyclerAdapter =
            ProductListAdapter(::onProductClick)
        listProducts.adapter = productRecyclerAdapter
        productRecyclerAdapter.setProducts(products)
        setupSearchView(productRecyclerAdapter)
    }

    private fun onProductClick(product: ProductItem) {
        viewModel.addCartItem(product)
    }

    private fun startScanZxing() {
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES)
        integrator.setPrompt("Scan a barcode")
        integrator.setBeepEnabled(true)
        integrator.setBarcodeImageEnabled(true)
        integrator.initiateScan()
    }

    private fun startExternalScanSunmi() {
        val intent = Intent(context, ScanningActivity::class.java)
        startActivityForResult(intent, EXTERNAL_SCANNER_CODE)
    }

    private fun startInternalScanSunmi() {
        val intent = Intent("com.summi.scan")

        intent.setPackage("com.sunmi.sunmiqrcodescanner")

        intent.putExtra(
            "CURRENT_PPI",
            0X0003
        )//The current preview resolution ,PPI_1920_1080 = 0X0001;PPI_1280_720 = 0X0002;PPI_BEST = 0X0003;

        intent.putExtra("PLAY_SOUND", true)// Prompt tone after scanning  ,default true

        intent.putExtra(
            "PLAY_VIBRATE",
            false
        )//vibrate after scanning,default false,only support M1 right now.

        intent.putExtra("IDENTIFY_INVERSE_QR_CODE", true)//Whether to identify inverse code

        intent.putExtra(
            "IDENTIFY_MORE_CODE",
            false
        )// Whether to identify several code，default false

        intent.putExtra(
            "IS_SHOW_SETTING",
            true
        )// Wether display set up button  at the top-right corner，default true

        intent.putExtra("IS_SHOW_ALBUM", true)// Wether display album，default true


        startActivityForResult(intent, INTERNAL_SCANNER_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == INTERNAL_SCANNER_CODE && data != null) {
            val bundle = data.extras
            val result = bundle!!.getSerializable("data") as ArrayList<HashMap<String, String>>

            val it: MutableIterator<Any>? = result.iterator()

            while (it!!.hasNext()) {
                val hashMap = it.next() as HashMap<String, String>

                val scanType = hashMap["TYPE"]!!
                val scanResult = hashMap["VALUE"]!!

                Log.i("sunmi_scanner_type: ", scanType)//this is the type of the code
//                Toast.makeText(activity, "Scan Type：$scanType", Toast.LENGTH_SHORT).show()

                Log.i("sunmi_scanner_result: ", scanResult)//this is the result of the code
//                Toast.makeText(activity, "Results：$scanResult", Toast.LENGTH_SHORT).show()

                val barcode: String = scanResult
                addScannedCartItem(barcode)

            }
        }
        if (requestCode == EXTERNAL_SCANNER_CODE && data != null) {
            if (resultCode == RESULT_OK) {
                val barcode: String = data.getStringExtra("product_barcode").toString()
                addScannedCartItem(barcode)

            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show()
            }

        }
        val result =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                scanResult(result)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    private fun scanResult(result: IntentResult?) {
        Toast.makeText(activity, "Scanned: " + result!!.contents, Toast.LENGTH_SHORT).show()
        val barcode: String = result.contents
        addScannedCartItem(barcode)
    }

    private fun addScannedCartItem(barcode: String) {
        val pdt = viewModel.productItems.find { p -> p.barcode == barcode }
        if (pdt != null) {
            val cartItem = CartItem(pdt)
            cartItem.let { it.let { it1 -> ShoppingCartRepository.addCartItem(it1) } }
            textProductFoundNotFound.text = "Product Found"
//            ShoppingCartRepository.addCartItem(cartItem)
        } else {
            textProductFoundNotFound.text = "Product Not Found"
        }

    }

    companion object {
        private const val INTERNAL_SCANNER_CODE: Int = 100
        private const val EXTERNAL_SCANNER_CODE: Int = 101
    }


}
