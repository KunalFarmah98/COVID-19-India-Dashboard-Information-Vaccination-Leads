package com.kunalfarmah.covid_19_info_dashboard.retrofit

import com.google.gson.annotations.SerializedName

data class LatestCasesResponse(

	@field:SerializedName("lastRefreshed")
	val lastRefreshed: String? = null,

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("lastOriginUpdate")
	val lastOriginUpdate: String? = null
)


data class Summary(

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("confirmedButLocationUnidentified")
	val confirmedButLocationUnidentified: Int? = null,

	@field:SerializedName("confirmedCasesForeign")
	val confirmedCasesForeign: Int? = null,

	@field:SerializedName("discharged")
	val discharged: Int? = null,

	@field:SerializedName("confirmedCasesIndian")
	val confirmedCasesIndian: Int? = null,

	@field:SerializedName("deaths")
	val deaths: Int? = null
)

data class Data(

		@field:SerializedName("summary")
		val summary: Summary? = null,

		@field:SerializedName("unofficial-summary")
		val unofficialSummary: List<UnofficialSummaryItem?>? = null,

		@field:SerializedName("regional")
		val regional: List<RegionalItem?>? = null
)

data class RegionalItem(

		@field:SerializedName("loc")
		val loc: String? = null,

		@field:SerializedName("confirmedCasesForeign")
		val confirmedCasesForeign: Int? = null,

		@field:SerializedName("discharged")
		val discharged: Int? = null,

		@field:SerializedName("confirmedCasesIndian")
		val confirmedCasesIndian: Int? = null,

		@field:SerializedName("deaths")
		val deaths: Int? = null,

		@field:SerializedName("totalConfirmed")
		val totalConfirmed: Int? = null
)

data class UnofficialSummaryItem(

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("recovered")
	val recovered: Int? = null,

	@field:SerializedName("active")
	val active: Int? = null,

	@field:SerializedName("source")
	val source: String? = null,

	@field:SerializedName("deaths")
	val deaths: Int? = null
)
