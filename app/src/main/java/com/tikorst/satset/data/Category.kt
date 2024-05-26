package com.tikorst.satset.data

import android.os.Parcelable
import com.tikorst.satset.R
import kotlinx.parcelize.Parcelize

@Parcelize
class Category(var name: String,var description: String, var logoResId: Int) : Parcelable
object CategoryData {
    val categoryList: List<Category>
        get() {
            val categories: MutableList<Category> = ArrayList()
            categories.add(Category("Electrical", "Comprehensive electrical services covering installations, repairs, and maintenance for residential and commercial properties. Includes wiring, circuit breaker installation, lighting fixture setup, and troubleshooting electrical issues.", R.drawable.electrical))
            categories.add(Category("Plumbing", "Extensive plumbing solutions encompassing water supply, drainage, and sewage systems. Services include pipe installations, leak repairs, drain cleaning, toilet and faucet maintenance, water heater installations, and sewer line upkeep.",R.drawable.plumbing))
            categories.add(Category("Air Conditioner","Specialized services dedicated to ensuring optimal cooling and air quality within residential and commercial spaces. Technicians offer installation, repair, and maintenance exclusively for air conditioning units, addressing issues such as refrigerant leaks, compressor malfunctions, airflow restrictions, and thermostat irregularities." ,R.drawable.ac))
//            categories.add(Category("Carpentry","Professional carpentry services for residential and commercial properties, including furniture assembly, cabinet installation, door and window repairs, and custom woodwork. Carpenters are skilled in handling various wood types and finishes to meet client specifications.",R.drawable.carpentry))
//            categories.add(Category("Painting","Expert painting services for interior and exterior surfaces of residential and commercial properties. Services include wall painting, ceiling painting, trim painting, and surface preparation. Painters use high-quality materials and techniques to ensure a flawless finish.",R.drawable.painting))
//            categories.add(Category("Cleaning","Comprehensive cleaning services for residential and commercial properties, covering general cleaning, deep cleaning, and specialized cleaning tasks. Services include dusting, vacuuming, mopping, sanitizing, and disinfecting various surfaces and areas.",R.drawable.cleaning))
//            categories.add(Category("Landscaping","Professional landscaping services for residential and commercial properties, including lawn care, garden maintenance, tree trimming, and hardscape installation. Landscapers offer design, installation, and maintenance services to enhance outdoor spaces.",R.drawable.landscaping))
//            categories.add(Category("Pest Control","Effective pest control services to eliminate and prevent infestations in residential and commercial properties. Services include inspection, treatment, and prevention of pests such as insects, rodents, termites, and bed bugs.",R.drawable.pest_control))
//            categories.add(Category("Roofing","Comprehensive roofing services for residential and commercial properties, including installation, repair, and maintenance of roofing systems. Roofers address issues such as leaks, damage, and deterioration to ensure the structural integrity and longevity of the roof.",R.drawable.roofing))
//            categories.add(Category("Security","Professional security services for residential and commercial properties, including alarm system installation, surveillance camera setup, access control systems, and security monitoring. Security experts provide customized solutions to enhance safety and protect against intruders.",R.drawable.security))
            categories.add(Category("Computer", "Offering installation, repair, and maintenance for computers in both residential and commercial properties. This includes addressing a variety of computer issues, managing software updates, conducting hardware checks, and performing system optimizations.", R.drawable.computer))
            categories.add(Category("Refrigerator", "Refrigerator services encompass installation, repair, and maintenance solutions for both residential and commercial settings. This category addresses various refrigerator concerns, including temperature management, ice maker repairs, and verification of door seals.", R.drawable.refrigerator))
            categories.add(Category("Washing Machine", "Extensive washing machine services encompassing installations, repairs, and maintenance for both residential and commercial properties. This category includes addressing a wide range of washer issues, managing cycle control, conducting drum cleaning, and performing door seal checks.", R.drawable.washer))
            return categories
        }
}