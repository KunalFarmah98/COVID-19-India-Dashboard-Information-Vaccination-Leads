package com.kunalfarmah.covid_19_info_dashboard.retrofit

import com.google.gson.annotations.SerializedName

data class LatestUnofficalResponse(

	@field:SerializedName("lastRefreshed")
	val lastRefreshed: String? = null,

	@field:SerializedName("data")
	val data: UnofficalData? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("lastOriginUpdate")
	val lastOriginUpdate: String? = null
)

data class Total(

	@field:SerializedName("recovered")
	val recovered: Int? = null,

	@field:SerializedName("active")
	val active: Int? = null,

	@field:SerializedName("confirmed")
	val confirmed: Int? = null,

	@field:SerializedName("deaths")
	val deaths: Int? = null
)

data class StatewiseItem(

	@field:SerializedName("recovered")
	val recovered: Int? = null,

	@field:SerializedName("active")
	val active: Int? = null,

	@field:SerializedName("state")
	val state: String? = null,

	@field:SerializedName("confirmed")
	val confirmed: Int? = null,

	@field:SerializedName("deaths")
	val deaths: Int? = null
)

data class UnofficalData(

	@field:SerializedName("lastRefreshed")
	val lastRefreshed: String? = null,

	@field:SerializedName("total")
	val total: Total? = null,

	@field:SerializedName("source")
	val source: String? = null,

	@field:SerializedName("statewise")
	val statewise: List<StatewiseItem?>? = null
)
