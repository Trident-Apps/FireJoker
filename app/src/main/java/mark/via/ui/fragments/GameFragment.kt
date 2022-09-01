package mark.via.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import mark.via.R
import mark.via.databinding.GameFragmentBinding

class GameFragment : Fragment() {
    private var _binding: GameFragmentBinding? = null
    private val binding get() = _binding!!

    private val TAG = "game"
    private val iv = binding.imageView
    private val iv2 = binding.imageView2
    private val iv3 = binding.imageView3
    private val iv4 = binding.imageView4
    private val gameText = binding.resultText
    private val gameBtn = binding.gameBtn

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GameFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listOf(
            iv, iv2, iv3
        ).forEach { imageView ->
            imageView.setOnClickListener {
                onClick(view as ImageView)
            }
        }

        gameBtn.setOnClickListener {
            repeatGame()
        }
    }

    private fun onClick(view: View) {

        showResult()

        val randomList = listOf(1, 2, 3).shuffled()
        Log.d(TAG, randomList[0].toString())
        if (randomList[0] == 1) {
            iv4.setImageResource(R.drawable.ic3)
            iv4.visibility = View.VISIBLE
            gameText.text = "You Won"
            gameText.visibility = View.VISIBLE
        } else {
            iv4.setImageResource(R.drawable.ic1)
            iv4.visibility = View.VISIBLE
            gameText.text = "You Loose"
            gameText.visibility = View.VISIBLE
        }
    }

    private fun showResult() {
        iv.visibility = View.INVISIBLE
        iv2.visibility = View.INVISIBLE
        iv3.visibility = View.INVISIBLE
        gameBtn.visibility = View.VISIBLE
    }

    private fun repeatGame() {
        iv.visibility = View.VISIBLE
        iv2.visibility = View.VISIBLE
        iv3.visibility = View.VISIBLE
        gameBtn.visibility = View.INVISIBLE
        gameText.visibility = View.INVISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}