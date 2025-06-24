package na.com.green.saampraat.ui.services.`object`

import na.com.green.saampraat.R
import na.com.green.saampraat.ui.services.model.Service

object ServicesData {

    fun getAllServices(): List<Service> {
        return listOf(
            Service(
                id = 1,
                name = "E-Voting Registration Portal",
                icon = R.drawable.ic_voting
            ),
            Service(
                id = 2,
                name = "Driver’s License Verification",
                icon = R.drawable.ic_licence
            ),
            Service(
                id = 3,
                name = "e-Health Records Viewer",
                icon = R.drawable.ic_health
            ),
            Service(
                id = 6,
                name = "Tax Refund Status Checker",
                icon = R.drawable.ic_tax
            ),
            Service(
                id = 7,
                name = "e-Signature",
                icon = R.drawable.ic_signature
            ),
            Service(
                id = 8,
                name = "Police Report Filing",
                icon = R.drawable.ic_police
            ),
            Service(
                id = 9,
                name = "Talent Discovery",
                icon = R.drawable.img
            ),
        )
    }
}