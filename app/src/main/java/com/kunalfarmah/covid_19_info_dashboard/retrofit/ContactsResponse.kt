package com.kunalfarmah.covid_19_info_dashboard.retrofit

data class ContactsResponse(
	val lastRefreshed: String? = null,
	val data: ContactsData? = null,
	val success: Boolean? = null,
	val lastOriginUpdate: String? = null
)

data class ContactsRegionalItem(
	val loc: String? = null,
	val number: String? = null
)

data class ContactsData(
	val contacts: Contacts? = null
)

data class Contacts(
	val regional: List<ContactsRegionalItem?>? = null,
	val primary: Primary? = null
)

data class Primary(
	val number: String? = null,
	val twitter: String? = null,
	val numberTollfree: String? = null,
	val facebook: String? = null,
	val media: List<String?>? = null,
	val email: String? = null
)

