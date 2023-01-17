package techradicle.expense.camera

interface ExpenseDestination {
    val route: String
}

object LoginScreen : ExpenseDestination {
    override val route: String = "login"
}

object OverViewScreen : ExpenseDestination {
    override val route: String = "dashboard"
}

object ImageUploadScreen : ExpenseDestination {
    override val route: String = "upload_receipt"

}