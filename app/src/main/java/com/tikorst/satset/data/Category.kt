package com.tikorst.satset.data

import android.os.Parcelable
import com.tikorst.satset.R
import kotlinx.parcelize.Parcelize


@Parcelize
class Category(
    var name: String,
    var description:String,
    var logoResId: Int,
    var services: List<Service> = emptyList(),
    var background: Int,
) : Parcelable
object CategoryData {
    val categoryList: List<Category>
        get() {
            val categories: MutableList<Category> = ArrayList()
            categories.add(Category("Electrical", "Comprehensive electrical services covering installations, repairs, and maintenance for residential and commercial properties. Includes wiring, circuit breaker installation, lighting fixture setup, and troubleshooting electrical issues.", R.drawable.electrical, ServicesData.electrical, R.drawable.electricalbg))
            categories.add(Category("Plumbing", "Extensive plumbing solutions encompassing water supply, drainage, and sewage systems. Services include pipe installations, leak repairs, drain cleaning, toilet and faucet maintenance, water heater installations, and sewer line upkeep.",R.drawable.plumbing, ServicesData.plumbing, R.drawable.plumbingbg))
            categories.add(Category("Air Conditioner","Specialized services dedicated to ensuring optimal cooling and air quality within residential and commercial spaces. Technicians offer installation, repair, and maintenance exclusively for air conditioning units, addressing issues such as refrigerant leaks, compressor malfunctions, airflow restrictions, and thermostat irregularities." ,R.drawable.ac, ServicesData.ac, R.drawable.acbg))
//            categories.add(Category("Carpentry","Professional carpentry services for residential and commercial properties, including furniture assembly, cabinet installation, door and window repairs, and custom woodwork. Carpenters are skilled in handling various wood types and finishes to meet client specifications.",R.drawable.carpentry))
//            categories.add(Category("Painting","Expert painting services for interior and exterior surfaces of residential and commercial properties. Services include wall painting, ceiling painting, trim painting, and surface preparation. Painters use high-quality materials and techniques to ensure a flawless finish.",R.drawable.painting))
//            categories.add(Category("Cleaning","Comprehensive cleaning services for residential and commercial properties, covering general cleaning, deep cleaning, and specialized cleaning tasks. Services include dusting, vacuuming, mopping, sanitizing, and disinfecting various surfaces and areas.",R.drawable.cleaning))
//            categories.add(Category("Landscaping","Professional landscaping services for residential and commercial properties, including lawn care, garden maintenance, tree trimming, and hardscape installation. Landscapers offer design, installation, and maintenance services to enhance outdoor spaces.",R.drawable.landscaping))
//            categories.add(Category("Pest Control","Effective pest control services to eliminate and prevent infestations in residential and commercial properties. Services include inspection, treatment, and prevention of pests such as insects, rodents, termites, and bed bugs.",R.drawable.pest_control))
//            categories.add(Category("Roofing","Comprehensive roofing services for residential and commercial properties, including installation, repair, and maintenance of roofing systems. Roofers address issues such as leaks, damage, and deterioration to ensure the structural integrity and longevity of the roof.",R.drawable.roofing))
//            categories.add(Category("Security","Professional security services for residential and commercial properties, including alarm system installation, surveillance camera setup, access control systems, and security monitoring. Security experts provide customized solutions to enhance safety and protect against intruders.",R.drawable.security))
            categories.add(Category("Computer", "Offering installation, repair, and maintenance for computers in both residential and commercial properties. This includes addressing a variety of computer issues, managing software updates, conducting hardware checks, and performing system optimizations.", R.drawable.computer, ServicesData.computer, R.drawable.computerbg))
            categories.add(Category("Refrigerator", "Refrigerator services encompass installation, repair, and maintenance solutions for both residential and commercial settings. This category addresses various refrigerator concerns, including temperature management, ice maker repairs, and verification of door seals.", R.drawable.refrigerator, ServicesData.refrigerator, R.drawable.refribg))
            categories.add(Category("Washing Machine", "Extensive washing machine services encompassing installations, repairs, and maintenance for both residential and commercial properties. This category includes addressing a wide range of washer issues, managing cycle control, conducting drum cleaning, and performing door seal checks.", R.drawable.washer, ServicesData.washer, R.drawable.washingbg))
            return categories
        }
}
@Parcelize
class Service(
    var name: String,
    var description:String,
    var logoResId: Int,
    var type: String
) : Parcelable

object ServicesData{
    fun findServiceDrawable(serviceType: String): Int? {
        val allServices = listOf(
            plumbing, electrical, ac, computer, refrigerator, washer
        ).flatten() // Flatten the list of lists into a single list of services

        val service = allServices.find { it.name.equals(serviceType, ignoreCase = true) }
        return service?.logoResId
    }
    val plumbing: List<Service>
        get(){
            val services: MutableList<Service> = ArrayList()
            services.add(Service("Installation", "Professional installation of plumbing systems for residential and commercial properties. Includes pipe installations, fixture setup, water heater installation, and sewage system configuration.", R.drawable.plumbing, "Plumbing"))
            services.add(Service("Repair", "Expert repair services for plumbing systems in residential and commercial properties. Includes fixing leaks, repairing pipes, unclogging drains, and addressing water pressure issues.", R.drawable.plumbing, "Plumbing"))
            services.add(Service("Maintenance", "Comprehensive maintenance services for plumbing systems in residential and commercial properties. Includes regular inspections, cleaning, and upkeep to prevent issues and ensure proper functioning.", R.drawable.plumbing, "Plumbing"))
            return services
        }
    val electrical: List<Service>
        get(){
            val services: MutableList<Service> = ArrayList()
            services.add(Service("Installation", "Professional installation of electrical systems for residential and commercial properties. Includes wiring, circuit breaker setup, lighting fixture installation, and electrical panel configuration.", R.drawable.electrical, "Electrical"))
            services.add(Service("Repair", "Expert repair services for electrical systems in residential and commercial properties. Includes troubleshooting, fixing wiring issues, repairing circuit breakers, and addressing lighting fixture problems.", R.drawable.electrical, "Electrical"))
            services.add(Service("Maintenance", "Comprehensive maintenance services for electrical systems in residential and commercial properties. Includes regular inspections, testing, and upkeep to ensure optimal performance and safety.", R.drawable.electrical, "Electrical"))
            return services
        }
    val ac: List<Service>
        get(){
            val services: MutableList<Service> = ArrayList()
            services.add(Service("Installation", "Professional installation of air conditioning units for residential and commercial properties. Includes setup, configuration, and testing to ensure optimal cooling and air quality.", R.drawable.ac, "AC"))
            services.add(Service("Repair", "Expert repair services for air conditioning units in residential and commercial properties. Includes fixing refrigerant leaks, repairing compressors, addressing airflow issues, and troubleshooting thermostat problems.", R.drawable.ac, "AC"))
            services.add(Service("Maintenance", "Comprehensive maintenance services for air conditioning units in residential and commercial properties. Includes regular inspections, cleaning, and upkeep to ensure efficient operation and extend the lifespan of the unit.", R.drawable.ac, "AC"))
            return services
        }
    val computer: List<Service>
        get() {
            val services: MutableList<Service> = ArrayList()
            services.add(
                Service(
                    "Installation",
                    "Professional installation of computer systems for residential and commercial properties. Includes hardware setup, software installation, network configuration, and system testing.",
                    R.drawable.computer,
                    "Computer"
                )
            )
            services.add(
                Service(
                    "Repair",
                    "Expert repair services for computer systems in residential and commercial properties. Includes diagnosing hardware issues, troubleshooting software problems, fixing connectivity errors, and optimizing system performance.",
                    R.drawable.computer,
                    "Computer"
                )
            )
            services.add(
                Service(
                    "Maintenance",
                    "Comprehensive maintenance services for computer systems in residential and commercial properties. Includes regular updates, virus scans, system checks, and performance optimizations to ensure smooth operation and data security.",
                    R.drawable.computer,
                    "Computer"
                )
            )
            return services
        }
    val refrigerator: List<Service>
        get() {
            val services: MutableList<Service> = ArrayList()
            services.add(
                Service(
                    "Installation",
                    "Professional installation of refrigerators for residential and commercial properties. Includes setup, configuration, temperature calibration, and testing to ensure proper cooling and food preservation.",
                    R.drawable.refrigerator,
                    "Refrigerator"
                )
            )
            services.add(
                Service(
                    "Repair",
                    "Expert repair services for refrigerators in residential and commercial properties. Includes fixing temperature issues, repairing ice makers, addressing door seal problems, and troubleshooting cooling malfunctions.",
                    R.drawable.refrigerator,
                    "Refrigerator"
                )
            )
            services.add(
                Service(
                    "Maintenance",
                    "Comprehensive maintenance services for refrigerators in residential and commercial properties. Includes cleaning, defrosting, checking door seals, and inspecting components to prevent breakdowns and ensure efficiency.",
                    R.drawable.refrigerator,
                    "Refrigerator"
                )
            )
            return services
        }
    val washer: List<Service>
        get() {
            val services: MutableList<Service> = ArrayList()
            services.add(
                Service(
                    "Installation",
                    "Professional installation of washing machines for residential and commercial properties. Includes setup, configuration, cycle calibration, and testing to ensure proper cleaning and fabric care.",
                    R.drawable.washer,
                    "Washing Machine"
                )
            )
            services.add(
                Service(
                    "Repair",
                    "Expert repair services for washing machines in residential and commercial properties. Includes fixing cycle control issues, repairing drum malfunctions, addressing door seal problems, and troubleshooting drainage errors.",
                    R.drawable.washer,
                    "Washing Machine"
                )
            )
            services.add(
                Service(
                    "Maintenance",
                    "Comprehensive maintenance services for washing machines in residential and commercial properties. Includes cleaning, descaling, checking door seals, and inspecting components to prevent breakdowns and ensure performance.",
                    R.drawable.washer,
                    "Washing Machine"
                )
            )
            return services
        }
}