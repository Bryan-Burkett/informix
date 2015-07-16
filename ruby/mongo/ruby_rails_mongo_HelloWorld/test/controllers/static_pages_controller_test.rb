require 'test_helper'

class StaticPagesControllerTest < ActionController::TestCase
  test "should get home" do
    get :home
    assert_response :success
  end

  test "should get info" do
    get :info
    assert_response :success
  end

  test "should get runTest" do
    get :runTest
    assert_response :success
  end

end
