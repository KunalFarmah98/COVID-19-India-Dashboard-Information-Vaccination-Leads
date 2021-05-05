package com.kunalfarmah.covid_19_info_dashboard.retrofit

import com.google.gson.annotations.SerializedName

data class DataResponse(

	@field:SerializedName("cases_time_series")
	val casesTimeSeries: List<CasesTimeSeriesItem?>? = null,

	@field:SerializedName("tested")
	val tested: List<TestedItem?>? = null,

	@field:SerializedName("statewise")
	val statewise: List<LatestStatewiseItem?>? = null
)

data class LatestStatewiseItem(

	@field:SerializedName("statenotes")
	val statenotes: String? = null,

	@field:SerializedName("recovered")
	val recovered: String? = null,

	@field:SerializedName("deltadeaths")
	val deltadeaths: String? = null,

	@field:SerializedName("migratedother")
	val migratedother: String? = null,

	@field:SerializedName("deltarecovered")
	val deltarecovered: String? = null,

	@field:SerializedName("active")
	val active: String? = null,

	@field:SerializedName("deltaconfirmed")
	val deltaconfirmed: String? = null,

	@field:SerializedName("state")
	val state: String? = null,

	@field:SerializedName("statecode")
	val statecode: String? = null,

	@field:SerializedName("confirmed")
	val confirmed: String? = null,

	@field:SerializedName("deaths")
	val deaths: String? = null,

	@field:SerializedName("lastupdatedtime")
	val lastupdatedtime: String? = null
)

data class CasesTimeSeriesItem(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("dailyrecovered")
	val dailyrecovered: String? = null,

	@field:SerializedName("dateymd")
	val dateymd: String? = null,

	@field:SerializedName("totalconfirmed")
	val totalconfirmed: String? = null,

	@field:SerializedName("totaldeceased")
	val totaldeceased: String? = null,

	@field:SerializedName("dailydeceased")
	val dailydeceased: String? = null,

	@field:SerializedName("totalrecovered")
	val totalrecovered: String? = null,

	@field:SerializedName("dailyconfirmed")
	val dailyconfirmed: String? = null
)

data class TestedItem(

	@field:SerializedName("source3")
	val source3: String? = null,

	@field:SerializedName("totaldosesadministered")
	val totaldosesadministered: String? = null,

	@field:SerializedName("source4")
	val source4: String? = null,

	@field:SerializedName("positivecasesfromsamplesreported")
	val positivecasesfromsamplesreported: String? = null,

	@field:SerializedName("healthcareworkersvaccinated2nddose")
	val healthcareworkersvaccinated2nddose: String? = null,

	@field:SerializedName("over60years1stdose")
	val over60years1stdose: String? = null,

	@field:SerializedName("registrationflwhcw")
	val registrationflwhcw: String? = null,

	@field:SerializedName("samplereportedtoday")
	val samplereportedtoday: String? = null,

	@field:SerializedName("source")
	val source: String? = null,

	@field:SerializedName("source2")
	val source2: String? = null,

	@field:SerializedName("totalrtpcrsamplescollectedicmrapplication")
	val totalrtpcrsamplescollectedicmrapplication: String? = null,

	@field:SerializedName("registrationonline")
	val registrationonline: String? = null,

	@field:SerializedName("frontlineworkersvaccinated1stdose")
	val frontlineworkersvaccinated1stdose: String? = null,

	@field:SerializedName("over60years2nddose")
	val over60years2nddose: String? = null,

	@field:SerializedName("testsconductedbyprivatelabs")
	val testsconductedbyprivatelabs: String? = null,

	@field:SerializedName("healthcareworkersvaccinated1stdose")
	val healthcareworkersvaccinated1stdose: String? = null,

	@field:SerializedName("to60yearswithco-morbidities1stdose")
	val to60yearswithcoMorbidities1stdose: String? = null,

	@field:SerializedName("dailyrtpcrsamplescollectedicmrapplication")
	val dailyrtpcrsamplescollectedicmrapplication: String? = null,

	@field:SerializedName("totalsessionsconducted")
	val totalsessionsconducted: String? = null,

	@field:SerializedName("updatetimestamp")
	val updatetimestamp: String? = null,

	@field:SerializedName("totalpositivecases")
	val totalpositivecases: String? = null,

	@field:SerializedName("registrationonspot")
	val registrationonspot: String? = null,

	@field:SerializedName("years1stdose")
	val years1stdose: String? = null,

	@field:SerializedName("seconddoseadministered")
	val seconddoseadministered: String? = null,

	@field:SerializedName("totalsamplestested")
	val totalsamplestested: String? = null,

	@field:SerializedName("totalindividualsregistered")
	val totalindividualsregistered: String? = null,

	@field:SerializedName("frontlineworkersvaccinated2nddose")
	val frontlineworkersvaccinated2nddose: String? = null,

	@field:SerializedName("firstdoseadministered")
	val firstdoseadministered: String? = null,

	@field:SerializedName("over45years1stdose")
	val over45years1stdose: String? = null,

	@field:SerializedName("totalindividualsvaccinated")
	val totalindividualsvaccinated: String? = null,

	@field:SerializedName("testedasof")
	val testedasof: String? = null,

	@field:SerializedName("over45years2nddose")
	val over45years2nddose: String? = null,

	@field:SerializedName("to60yearswithco-morbidities2nddose")
	val to60yearswithcoMorbidities2nddose: String? = null,

	@field:SerializedName("totalindividualstested")
	val totalindividualstested: String? = null
)
