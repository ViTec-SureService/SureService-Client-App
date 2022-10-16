package vitec.sureservice.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import vitec.sureservice.ui.MainScreen
import vitec.sureservice.ui.bookAnAppointment.BookAnAppointment
import vitec.sureservice.ui.bookAnAppointment.DetailBookAnAppointment
import vitec.sureservice.ui.bookAnAppointment.ServiceRequestViewModel
import vitec.sureservice.ui.login.LogIn
import vitec.sureservice.ui.login.LogInViewModel
import vitec.sureservice.ui.reservation.Reservation
import vitec.sureservice.ui.service.Service
import vitec.sureservice.ui.service.ServiceViewModel
import vitec.sureservice.ui.settings.Settings
import vitec.sureservice.ui.signup.SignUp
import vitec.sureservice.ui.signup.SignUpViewModel
import vitec.sureservice.ui.technicianProfile.TechnicianProfile


@ExperimentalAnimationApi
@Composable
fun Navigation(startDestination: String, navController: NavHostController) {

    BoxWithConstraints {
        AnimatedNavHost(
            navController = navController,
            startDestination = startDestination
        ){
            addLogin(navController)
            addSignUp(navController)
            addHome(navController)
            addService(navController)
            addReservation()
            addSettings()
            addTechnicianProfile(navController)
            addBookAnAppointment(navController)
            addDetailBookAnAppointment()
        }
    }
}


@ExperimentalAnimationApi
fun NavGraphBuilder.addLogin(
    navController: NavHostController
){
    composable(
        route = Destinations.Login.route,
    ){
        val viewModel: LogInViewModel = hiltViewModel()

        if(viewModel.state.value.successLogIn){
            LaunchedEffect(key1 = Unit){
                navController.navigate(
                    Destinations.Home.route
                ){
                    popUpTo(Destinations.Login.route){
                        inclusive = true
                    }
                }
            }
        } else {
            LogIn(
                state = viewModel.state.value,
                onLogIn = viewModel::login,
                onNavigateToRegister = {
                    navController.navigate(Destinations.Signup.route)
                    {
                        popUpTo(Destinations.Login.route)
                    }
                },
                onDismissDialog = viewModel::hideErrorDialog
            )
        }
    }
}

@ExperimentalAnimationApi
fun NavGraphBuilder.addSignUp(
    navController: NavHostController
){
    composable(
        route = Destinations.Signup.route,
    ){
        val viewModel: SignUpViewModel = hiltViewModel()

        if(viewModel.state.value.successSignUp){
            LaunchedEffect(key1 = Unit){
                navController.navigate(
                    Destinations.Home.route
                ){
                    popUpTo(Destinations.Login.route){
                        inclusive = true
                    }
                }
            }
        } else {
            SignUp(
                state = viewModel.state.value,
                onSignUp = viewModel::signup,
                onLogIn = {
                    navController.navigate(Destinations.Login.route){
                        popUpTo(Destinations.Signup.route){
                            inclusive = true
                        }
                    }
                },
                onDismissDialog = viewModel::hideErrorDialog
            )
        }

    }
}

@ExperimentalAnimationApi
fun NavGraphBuilder.addHome(navController: NavHostController) {
    composable(
        route = Destinations.Home.route,
        arguments = Destinations.Home.arguments
    ){
        val loginViewModel: LogInViewModel = hiltViewModel()
        MainScreen{
            loginViewModel.clientDao.deleteClient(loginViewModel.client)
            navController.navigate(Destinations.Login.route){
                popUpTo(Destinations.Home.route){
                    inclusive = true
                }
            }
        }
    }
}

@ExperimentalAnimationApi
fun NavGraphBuilder.addService(navController: NavHostController) {
    composable(
        route = Destinations.Service.route,
        arguments = Destinations.Service.arguments
    ){
        val serviceViewModel: ServiceViewModel = hiltViewModel()
        Service(serviceViewModel){
            navController.navigate(
                Destinations.TechnicianProfile.createRoute(it)
            )
        }
    }
}

@ExperimentalAnimationApi
fun NavGraphBuilder.addReservation() {
    composable(
        route = Destinations.Reservation.route,
        arguments = Destinations.Reservation.arguments
    ){
        Reservation()
    }
}

@ExperimentalAnimationApi
fun NavGraphBuilder.addSettings() {
    composable(
        route = Destinations.Settings.route,
        arguments = Destinations.Settings.arguments
    ){
        Settings()
    }
}

@ExperimentalAnimationApi
fun NavGraphBuilder.addTechnicianProfile(navController: NavHostController) {
    composable(
        route = Destinations.TechnicianProfile.route
    ){ navBackStackEntry ->
        val technicianId = navBackStackEntry.arguments?.getString("technicianId")
        val serviceViewModel: ServiceViewModel = hiltViewModel()
        TechnicianProfile(Integer.parseInt(technicianId!!), serviceViewModel)
        {
            navController.navigate(
                Destinations.BookAnAppointment.createRoute(it)
            )
        }
    }
}

@ExperimentalAnimationApi
fun NavGraphBuilder.addBookAnAppointment(navController: NavHostController){
    composable(
        route = Destinations.BookAnAppointment.route
    ){ navBackStackEntry ->
        val technicianId = navBackStackEntry.arguments?.getString("technicianId")
        val serviceViewModel: ServiceViewModel = hiltViewModel()
        val serviceRequestViewModel: ServiceRequestViewModel = hiltViewModel()
        BookAnAppointment(Integer.parseInt(technicianId!!), serviceViewModel, serviceRequestViewModel, navController)
    }
}

@ExperimentalAnimationApi
fun NavGraphBuilder.addDetailBookAnAppointment(){
    composable(
        route = Destinations.DetailBookAnAppointment.route
    ){ navBackStackEntry ->
        val date = navBackStackEntry.arguments?.getString("date")
        val technicianName = navBackStackEntry.arguments?.getString("technicianName")
        val technicianLastName = navBackStackEntry.arguments?.getString("technicianLastName")
        val technicianDistrict = navBackStackEntry.arguments?.getString("technicianDistrict")
        val serviceRequestId = navBackStackEntry.arguments?.getString("serviceRequestId")
        DetailBookAnAppointment(date!!, technicianName!!, technicianLastName!!, technicianDistrict!!, serviceRequestId!!)
    }
}