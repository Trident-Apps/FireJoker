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
            binding.imageView, binding.imageView2, binding.imageView3,
        ).forEach { imageView ->
            imageView.setOnClickListener {
                playGame()
            }
        }
        binding.gameBtn.setOnClickListener {
            repeatGame()
        }
    }

    private fun playGame() {

        showResult()
        val randomList = listOf(1, 2, 3).shuffled()
        if (randomList[0] == 1) {
            binding.imageView4.visibility = View.VISIBLE
            binding.resultText.visibility = View.VISIBLE
            binding.imageView4.setImageResource(R.drawable.ic3)
            binding.resultText.text = "You Won"
        } else {
            binding.imageView4.visibility = View.VISIBLE
            binding.resultText.visibility = View.VISIBLE
            binding.imageView4.setImageResource(R.drawable.ic1)
            binding.resultText.text = "You Loose"
        }
    }

    private fun showResult() {
        binding.imageView.visibility = View.INVISIBLE
        binding.imageView2.visibility = View.INVISIBLE
        binding.imageView3.visibility = View.INVISIBLE
        binding.gameBtn.visibility = View.VISIBLE
    }

    private fun repeatGame() {
        binding.imageView.visibility = View.VISIBLE
        binding.imageView2.visibility = View.VISIBLE
        binding.imageView3.visibility = View.VISIBLE
        binding.imageView4.visibility = View.INVISIBLE
        binding.gameBtn.visibility = View.INVISIBLE
        binding.gameBtn.visibility = View.INVISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}