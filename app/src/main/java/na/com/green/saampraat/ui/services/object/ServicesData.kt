package na.com.green.saampraat.ui.services.`object`

import na.com.green.saampraat.R
import na.com.green.saampraat.ui.services.model.Service

object ServicesData {

    fun getAllServices(): List<Service> {
        return listOf(
            Service(
                id = 1,
                name = "Tax Refund Status Checker",
                icon = R.drawable.ic_tax
            ),
            Service(
                id = 2,
                name = "Driver’s License Verification",
                icon = R.drawable.ic_licence
            ),
            Service(
                id = 3,
                name = "E-Voting Portal",
                icon = R.drawable.ic_voting
            ),
            Service(
                id = 4,
                name = "Police Report Filing",
                icon = R.drawable.ic_police
            ),
            Service(
                id = 5,
                name = "Business Registration",
                icon = R.drawable.ic_business
            ),
            Service(
                id = 6,
                name = "Property Tax Payment",
                icon = R.drawable.ic_property
            ),
            Service(
                id = 7,
                name = "e-Health Records Viewer",
                icon = R.drawable.ic_health
            ),
            Service(
                id = 8,
                name = "e-Signature",
                icon = R.drawable.ic_signature
            ),
            Service(
                id = 9,
                name = "Social Security Benefits",
                icon = R.drawable.ic_ss
            ),
            Service(
                id = 10,
                name = "Birth Certificate",
                icon = R.drawable.ic_birth
            ),
            Service(
                id = 11,
                name = "Talent Discovery",
                icon = R.drawable.img
            ),
            Service(
                id = 12,
                name = "Student Financial Aid",
                icon = R.drawable.ic_study
            ),
            Service(
                id = 13,
                name = "Passport Portal",
                icon = R.drawable.ic_passport
            ),
            Service(
                id = 14,
                name = "Water and Electricity Bills",
                icon = R.drawable.ic_bills
            )
        )
    }
}