package com.kunalfarmah.covid_19_info_dashboard.retrofit

import com.google.gson.annotations.SerializedName

data class HistoryResponse(

	@field:SerializedName("lastRefreshed")
	val lastRefreshed: String? = null,

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("lastOriginUpdate")
	val lastOriginUpdate: String? = null
)

data class DataItem(

	@field:SerializedName("summary")
	val summary: HistoricalSummary? = null,

	@field:SerializedName("regional")
	val regional: List<RegionalItem?>? = null,

	@field:SerializedName("day")
	val day: String? = null
)


data class HistoricalSummary(

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
