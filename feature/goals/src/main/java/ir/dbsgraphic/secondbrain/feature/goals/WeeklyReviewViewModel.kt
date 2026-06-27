package ir.dbsgraphic.secondbrain.feature.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.dbsgraphic.secondbrain.core.data.ReviewRepository
import ir.dbsgraphic.secondbrain.core.data.WeeklyReview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class WeeklyReviewViewModel @Inject constructor(
    reviewRepository: ReviewRepository,
) : ViewModel() {

    val review: StateFlow<WeeklyReview> = reviewRepository.observeWeeklyReview()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WeeklyReview())
}
